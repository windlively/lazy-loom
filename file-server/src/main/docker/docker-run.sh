docker run \
  -e ARGS='--spring.profiles.active=PRO' \
  -e VM_OPTS="-Dspring.config.location=/data/application.yml" \
  --name sundry-booklet \
  --network host \
  -v /usr/docker-app/sundry-booklet/application.yml:/data/application.yml \
  -d windlively/sundry-booklet
