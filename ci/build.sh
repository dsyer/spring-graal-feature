#!/usr/bin/env sh

set -e

# Beginning of GraalVM dev build install

GRAALVM_VERSION=20.1.0-dev
GRAALVM_BUILD=20200125-1203
JAVA_VERSION=java8
GRAALVM_PKG=https://github.com/graalvm/graalvm-ce-dev-builds/releases/download/${GRAALVM_VERSION}_${GRAALVM_BUILD}/graalvm-ce-${JAVA_VERSION}-linux-amd64-${GRAALVM_VERSION}.tar.gz

export ENV LANG=en_US.UTF-8
export JAVA_HOME=/opt/graalvm-ce-${JAVA_VERSION}-${GRAALVM_VERSION}/

yum update -y oraclelinux-release-el7 \
    && yum install -y oraclelinux-developer-release-el7 oracle-softwarecollection-release-el7 \
    && yum-config-manager --enable ol7_developer \
    && yum-config-manager --enable ol7_developer_EPEL \
    && yum-config-manager --enable ol7_optional_latest \
    && yum install -y bzip2-devel ed gcc gcc-c++ gcc-gfortran gzip file fontconfig less libcurl-devel make openssl openssl-devel readline-devel tar vi which xz-devel zlib-devel \
    && yum install -y glibc-static libcxx libcxx-devel libstdc++-static zlib-static

fc-cache -f -v

cp gu-wrapper.sh /usr/local/bin/gu

set -eux \
    && curl --fail --silent --location --retry 3 ${GRAALVM_PKG} \
    | gunzip | tar x -C /opt/ \
    && mkdir -p "/usr/java" \
    && ln -sfT "$JAVA_HOME" /usr/java/default \
    && ln -sfT "$JAVA_HOME" /usr/java/latest \
    && for bin in "$JAVA_HOME/bin/"*; do \
        base="$(basename "$bin")"; \
        [ ! -e "/usr/bin/$base" ]; \
        alternatives --install "/usr/bin/$base" "$base" "$bin" 20000; \
    done \
    && chmod +x /usr/local/bin/gu

java -version
gu install native-image

# End of GraalVM dev build install

npm install tty-table -g
yum install -y unzip wget procps bc perl util-linux golang git

go get github.com/fullstorydev/grpcurl
go install github.com/fullstorydev/grpcurl/cmd/grpcurl

wget https://repo1.maven.org/maven2/org/apache/maven/apache-maven/3.5.3/apache-maven-3.5.3-bin.zip
unzip apache-maven-3.5.3-bin.zip
export PATH=$PATH:`pwd`/apache-maven-3.5.3/bin:/root/go/bin:/opt/graalvm-ce-java8-19.3.1/jre/languages/js/bin

cd spring-graal-native
./build-feature.sh
./build-key-samples.sh