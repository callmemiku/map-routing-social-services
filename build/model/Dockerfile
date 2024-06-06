FROM ubuntu:20.04

RUN \
    set -eux              ; \
    apt-get -qq update    ; \
    apt-get -yqq install    \
        make                \
        python3 python3-pip \
    ; \
    pip install                       \
        numpy pandas matplotlib torch \
        openpyxl