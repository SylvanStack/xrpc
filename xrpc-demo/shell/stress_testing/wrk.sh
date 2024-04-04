#!/bin/bash
echo hello wrk
# 验证一下，是否安装成功
wrk -v
# 利用 wrk 对 http://127.0.0.1:8081/health/check 发起压力测试，线程数为 12，模拟 400 个并发请求，持续 30 秒
wrk -t12 -c400 -d30s http://127.0.0.1:8081/health/check