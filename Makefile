.PHONY: pkg

version=$(shell git rev-parse --short HEAD)

pkg:
	docker build . -t bookmarks:$(version)