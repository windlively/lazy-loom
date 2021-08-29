docker run \
  -e VM_OPT='-Dfile-server.workspace=/data -Dserver.port=8000' \
  -p 8000:8000 \
  -v /usr/file-server:/data \
  -d \
  --name file-server \
  windlively/file-server
