package main

import (
	"bytes"
	"io/ioutil"
	"log"
	"net"
	"net/http"
	"strings"

	"github.com/AdguardTeam/gomitmproxy"
	"github.com/AdguardTeam/gomitmproxy/proxyutil"
)

func setUpProxy() *gomitmproxy.Proxy {
	proxy := gomitmproxy.NewProxy(gomitmproxy.Config{
		ListenAddr: &net.TCPAddr{
			IP:   net.IPv4(0, 0, 0, 0),
			Port: 8080,
		},
		// Username: "username",
		// Password: "password",
		//  Client -intercept->  Server
		OnRequest: func(session *gomitmproxy.Session) (request *http.Request, response *http.Response) {
			req := session.Request()
			log.Printf("onRequest: %s %s", req.Method, req.URL.String())
			if req.URL.Port() == "7999" {
				body, err := ioutil.ReadAll(req.Body)
				if err != nil {
					panic(err)
				}

				if strings.Contains(string(body), "secret") {
					log.Printf("[<**] Got dangerous request: %s", body)
				}

				req.Body = ioutil.NopCloser(bytes.NewReader(body))
			}

			return nil, nil
		},
		//  Server -intercept->  Client
		OnResponse: func(session *gomitmproxy.Session) *http.Response {
			res := session.Response()
			req := session.Request()

			log.Printf("onResponse: %s", req.URL.String())
			if req.URL.Port() == "7999" {
				b, err := proxyutil.ReadDecompressedBody(res)
				// Close the original body
				_ = res.Body.Close()
				if err != nil {
					return proxyutil.NewErrorResponse(req, err)
				}

				// Use latin1 before modifying the body
				// Using this 1-byte encoding will let us preserve all original characters
				// regardless of what exactly is the encoding
				body, err := proxyutil.DecodeLatin1(bytes.NewReader(b))
				if err != nil {
					return proxyutil.NewErrorResponse(session.Request(), err)
				}

				if strings.Contains(string(body), "secret") {
					log.Printf("[**>] Got dangerous response: %s", body)
				}

				//  Keeping the original body
				modifiedBody, err := proxyutil.EncodeLatin1(body)
				if err != nil {
					return proxyutil.NewErrorResponse(session.Request(), err)
				}

				res.Body = ioutil.NopCloser(bytes.NewReader(modifiedBody))
				res.Header.Del("Content-Encoding")
				res.ContentLength = int64(len(modifiedBody))
				return res
			}
			return res
		},
	})
	err := proxy.Start()
	if err != nil {
		log.Fatal(err)
	}
	return proxy
}
