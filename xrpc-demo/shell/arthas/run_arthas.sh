#!/bin/bash
java -jar arthas-boot.jar --repo-mirror aliyun --use-http1

# 输入 dashboard，按回车/enter，会展示当前进程的信息，按ctrl+c可以中断执行。

# 通过 thread 命令来获取到math-game进程的 Main Class

# thread 1 | grep 'main('