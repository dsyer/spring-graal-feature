FROM ubuntu:bionic

RUN apt-get -y update && apt-get -y install build-essential wget

RUN cd /opt \
 && wget https://ftp.gnu.org/gnu/gdb/gdb-8.3.1.tar.xz \
 && tar -xf gdb-8.3.1.tar.xz \
 && cd gdb-8.3.1 \
 && ./configure && make && cp gdb/gdb /usr/bin \
 && rm ../gdb-8.3.1.tar.xz
