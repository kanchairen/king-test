#!/bin/bash

SELF=$(cd $(dirname $0); pwd -P)/$(basename $0)
null_buffer=/dev/null
should_exit=./flag.exit
pid_file=./flag.pid
log=lky.log
wait_time=5
application='.:../lib/* com.lky.LkyApplication'

#检查进程是否存在,传入进程号
check_process() {
    if [ "$1" = "" ]; then
        return 1
    fi

    kill -0 $1 &> $null_buffer
    if [ $? -eq 0 ]; then
        return 0
    else
        return 1
    fi
}

# 等待进程退出
wait_process() {
    while true;
    do
        kill -0 $1 &> $null_buffer
        if [ $? != 0 ]; then
            return
        fi

        sleep 0.5s
    done
}

#等待进程退出,$1为进程PID，$2为最长等待时间单位s
wait_for_sometime() {
    local wait=0
    kill -0 $1 &> $null_buffer
    while ( [ $? = 0 ] && [ $wait -lt $2 ] ); do
        sleep 1s
        wait=$((wait+1))
        kill -0 $1 &> $null_buffer
    done

    kill -0 $1 &> $null_buffer
    if [ $? -ne 0 ]; then
        return 0
    else
        return 1
    fi
}

#启动liegou服务程序
liegou_start() {
    if [[ "r" = "$1" ]]; then
        echo "Start lky by remote"
        nohup java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5013 -cp ${application} >$null_buffer 2>&1 &
    else
        echo "Start lky"
        nohup java -cp ${application} >$null_buffer 2>&1 &
    fi
}

#关闭liegou服务程序
liegou_stop() {
    echo "Stop lky"
    local checkResult=1
    if [ -f $pid_file ]; then
        check_process `cat $pid_file`
        checkResult=$?
    else
        echo "Have no process file"
        return 1
    fi

    if [ $checkResult -eq 1 ]; then
        echo "$1 has been stopped, skip!!!"
        return 0
    fi

    local PID=$(cat "$pid_file")
    touch "$should_exit"

    #等待进程退出
    wait_for_sometime $PID $wait_time
    if [ $? -ne 0 ]; then
        echo "service $1 does't exit in 5s, try to kill its process"
        kill -9 $PID &> $null_buffer
    fi

    wait_process $PID

    #删除进程文件
    rm -f $pid_file &> $null_buffer
    rm -f $should_exit &> $null_buffer

    echo "Server $PID has exit!!!"
}

#liegou服务程序状态
liegou_status() {
    local checkResult=1
    if [ -f $pid_file ]; then
        check_process `cat $pid_file`
        checkResult=$?
    else
        echo "Have no process file"
        return 1
    fi

    if [ $checkResult -eq 1 ]; then
        echo "Program not running!!!"
        return 1
    else
        echo "Program is running!!!"
        return 0
    fi
}


case "${1:-''}" in
    "start")
        liegou_start $2
        ;;
    "stop")
        liegou_stop
        ;;
    "restart")
        echo "restart lky"
        liegou_stop
        liegou_start $2
        ;;
    "status")
        liegou_status
        ;;
    *)
        echo "Usage: $SELF start|start r|stop|restart|restart r|status"
        exit 1
        ;;
esac

exit 0