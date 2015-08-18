#!/bin/bash

set -e

TESTAPPNAME=gotquotes
MARATHONS=( marathon_aws marathon_gce )

DEFAULT_MARATHON=${MARATHONS[0]}

function cleanup() {
    for M in "${MARATHONS[@]}"
    do
        curl -X DELETE -H 'Content-Type: application/json' http://${M}:8080/v2/apps/$TESTAPPNAME &> /dev/null
    done
}

function deploy_app() {
    APP_DEFINITION=$1
    TRIATHLON=$(get_triathlon_host)
    curl -X POST -H 'Content-Type: application/json' http://${TRIATHLON}:9090/v2/apps -d@${APP_DEFINITION} &> /dev/null
}

function get_triathlon_host() {
    curl -X GET -H 'Content-Type: application/json' http://${DEFAULT_MARATHON}:8080/v2/apps/triathlon 2> /dev/null | python -m json.tool | grep host | cut -f4 -d'"'
}

function fail() {
    echo "TESTS FAILED: ${1}"
    exit 1
}

function running_instances() {
    curl -X GET -H 'Content-Type: application/json' http://${MARATHONS[$1]}:8080/v2/apps/${TESTAPPNAME} 2> /dev/null | python -c "import json, sys; print json.load(sys.stdin)['app']['tasksRunning']"
}

# ---------------------------------------------------------------------

function test_cluster() {
    echo Testing CLUSTER operator...

    echo --- Deploy to datacenter_1:

    deploy_app ./tests/cluster/dc1.json

    sleep 10 

    RUNNING_INSTANCES=$(running_instances 0)
    if [ "$RUNNING_INSTANCES" -ne 1 ]
    then
    fail "CLUSTER 1"
    fi

    echo "OK"

    cleanup

    echo --- Deploy to datacenter_2:

    deploy_app ./tests/cluster/dc2.json

    sleep 10

    RUNNING_INSTANCES=$(running_instances 1)
    if [ "$RUNNING_INSTANCES" -ne 2 ]
    then
    fail "CLUSTER 2"
    fi

    echo "OK"

    cleanup

    sleep 3
}

# ---------------------------------------------------------------------

function test_group_by() {
    echo Testing GROUP_BY operator...

    echo --- Deploy 2 instances to all dcs:

    deploy_app ./tests/group_by/test1.json

    sleep 10

    RUNNING_INSTANCES=$(running_instances 0)
    if [ "$RUNNING_INSTANCES" -ne 1 ]
    then
        fail "GROUP_BY 1"
    fi

    RUNNING_INSTANCES=$(running_instances 1)
    if [ "$RUNNING_INSTANCES" -ne 1 ]
    then
        fail "GROUP_BY 2"
    fi

    echo "OK"

    cleanup

    sleep 3

    echo --- Deploy 4 instances to all dcs:

    deploy_app ./tests/group_by/test2.json

    sleep 10

    RUNNING_INSTANCES=$(running_instances 0)
    if [ "$RUNNING_INSTANCES" -ne 2 ]
    then
        fail "GROUP_BY 3"
    fi

    RUNNING_INSTANCES=$(running_instances 1)
    if [ "$RUNNING_INSTANCES" -ne 2 ]
    then
        fail "GROUP_BY 4"
    fi

    echo "OK"

    echo PAUSED
    read
    cleanup

    sleep 3

    echo --- Deploy 3 instances to all dcs:

    deploy_app ./tests/group_by/test3.json

    sleep 10

    RUNNING_INSTANCES=$(running_instances 0)
    if [ "$RUNNING_INSTANCES" -ne 1 ]
    then
        fail "GROUP_BY 5"
    fi

    RUNNING_INSTANCES=$(running_instances 1)
    if [ "$RUNNING_INSTANCES" -ne 2 ]
    then
        fail "GROUP_BY 6"
    fi

    echo "OK"

    cleanup

    sleep 3

    echo --- Deploy 5 instances to all dcs:
}

# ---------------------------------------------------------------------

function test_unique() {
    echo Testing UNIQUE operator...

    echo --- Deploy 2 instances into 2 dcs

    deploy_app ./tests/unique/test1.json

    sleep 10 

    RUNNING_INSTANCES=$(running_instances 0)
    if [ "$RUNNING_INSTANCES" -ne 1 ]
    then
    fail "UNIQUE 1"
    fi

    echo "OK"

    cleanup

    echo --- Deploy 4 instances into 2 dcs

    deploy_app ./tests/unique/test2.json

    sleep 10

    RUNNING_INSTANCES=$(running_instances 1)
    if [ "$RUNNING_INSTANCES" -ne 1 ]
    then
    fail "UNIQUE 2"
    fi

    echo "OK"

    cleanup

    sleep 3
}

# ---------------------------------------------------------------------


cleanup
sleep 3
#test_cluster
test_group_by
#test_unique


