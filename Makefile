.PHONY: build publish prepareVm deploy

version=$(shell git rev-parse --short HEAD)

build:
	docker build . -t bookmarks:$(version)

publish:
	# push to a remote container registry

prepareVm:
	# install docker on linux vm

deploy:
	# deploy to a container host
