expect登陆别的机器并输入多行文本到文件

#!/bin/bash
#!/usr/bin/expect
hostname="127.0.0.1"
password="pwd"
content="NB JUST IS NB\rI am wh"
expect<<EOF
spawn ssh root@${hostname}
expect {
"(yes/no)" {send "yes\r";exp_continue}
"*password" {send "$password\r"}
}
expect "*#"
send "mkdir -p /usr/local/wh\n"
expect "*#"
send "cd /usr/local/wh\n"
expect "*#"
send "touch test.txt\n"
expect "*#"
# 必须得加转义字符\,否则会报extra characters after close-quote
send "echo \"$content\" > ./test.txt \n"
# 匹配进入后的页面
expect "*#"
# 等待结束
expect eof
# expect结束标志，EOF前后不能有空格
EOF

https://www.cnblogs.com/TDXYBS/p/11012089.html
https://blog.csdn.net/lixinkuan328/article/details/111991344
