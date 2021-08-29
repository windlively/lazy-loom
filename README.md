# leisure
### 此项目用于提交临时的脚本代码或者Demo项目、小工具等
## file-server
### 构建Docker镜像
```shell
# 在file-server项目目录下执行

# 打包
mvn clean package
# 构建docker镜像
mvn docker:build
# docker-run示例
docker run -e VM_OPT='-Dfile-server.workspace=/data -Dserver.port=8000' -p 8000:8000 -v /Users/windlively/file-server:/data -d --name file-server windlively/file-server
```
### 直接从Docker拉取镜像运行
```shell
docker pull windlively/file-server
```
