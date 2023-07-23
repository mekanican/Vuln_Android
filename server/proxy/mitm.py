from mitmproxy import http
import logging

# f = open("/tmp/log.txt", "a")

def request(flow: http.HTTPFlow) -> None:
    if flow.request.port == 7999 and "/secret" == flow.request.path:
        if flow.request.urlencoded_form:
            x = flow.request.urlencoded_form["secret"]
            print("[?] found a secret from client -> server: " + x + "\n")

def response(flow: http.HTTPFlow) -> None:
    if flow.request.port == 7999 and "/secret" == flow.request.path:
        print("[?] found a secret from server -> client: " + flow.response.content.decode() + "\n")