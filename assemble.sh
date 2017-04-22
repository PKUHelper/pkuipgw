#!/bin/sh
cd `dirname "$0"`
PROJECT_NAME=$(echo $(head -n 1 project.clj) | sed 's/^(defproject \([^ ]*\) "\([^ ]*\)" *$/\1-\2/g')
rm -rf release
mkdir -p release/${PROJECT_NAME}/bin
cp target/uberjar/${PROJECT_NAME}-standalone.jar release/${PROJECT_NAME}/${PROJECT_NAME}.jar
sed "s/{PROJECT_NAME}/${PROJECT_NAME}/g" resources/pkuipgw.sh.sample > release/${PROJECT_NAME}/bin/pkuipgw
sed "s/{PROJECT_NAME}/${PROJECT_NAME}/g" resources/install.sh.sample > release/${PROJECT_NAME}/install
chmod +x release/${PROJECT_NAME}/bin/pkuipgw
chmod +x release/${PROJECT_NAME}/install
cd release
tar -zcvf ${PROJECT_NAME}.tgz ${PROJECT_NAME}