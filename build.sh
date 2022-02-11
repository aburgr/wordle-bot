set -e

mvn spring-boot:build-image

version=$(cat pom.xml | grep -oPm1 "(?<=<version>)[^<]+")
repo=andiburgr/wordle-bot

docker tag wordle-bot:$version $repo:$version
docker push $repo:$version