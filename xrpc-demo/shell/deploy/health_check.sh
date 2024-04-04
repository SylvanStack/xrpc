#!/bin/bash

# 等待应用启动的时间
APP_START_TIMEOUT=100
# 应用端口
APP_PORT=8081
# 应用健康检查URL
HEALTH_CHECK_URL=http://127.0.0.1:${APP_PORT}/health/check

health_check() {
    exp_time=0
    echo "checking ${HEALTH_CHECK_URL}"
    while true
        do
            # shellcheck disable=SC2006
            # shellcheck disable=SC1083
            status_code=`/usr/bin/curl -L -o /dev/null --connect-timeout 5 -s -w %{http_code}  ${HEALTH_CHECK_URL}`
            # shellcheck disable=SC2181
            if [ "$?" != "0" ]; then
               echo -n -e "\r application not started"
            else
                echo "code is $status_code"
                if [ "$status_code" == 200 ];then
                   break
                fi
            fi
            sleep 1
            ((exp_time++))

            echo -e "\r Wait app to pass health check: $exp_time ..."

            if [ "$exp_time" -gt ${APP_START_TIMEOUT} ]; then
                echo 'app start failed'
               exit 1
            fi
        done
    echo "check ${HEALTH_CHECK_URL} success"
}

health_check