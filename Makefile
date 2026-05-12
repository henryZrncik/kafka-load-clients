ORG ?= henrichz
REGISTRY ?= quay.io
TAG ?= $(shell git describe --tags --always)
PRODUCER_IMAGE ?= $(REGISTRY)/$(ORG)/kafka-load-producer:$(TAG)
CONSUMER_IMAGE ?= $(REGISTRY)/$(ORG)/kafka-load-consumer:$(TAG)

.PHONY: all build test docker_build docker_push clean

all: build docker_build docker_push

build:
	mvn clean package -DskipTests

test:
	mvn test

docker_build: build
	@echo "Building multi-arch: $(PRODUCER_IMAGE)"
	-podman manifest rm $(PRODUCER_IMAGE) 2>/dev/null
	podman manifest create $(PRODUCER_IMAGE)
	podman build --platform linux/amd64,linux/arm64 --manifest $(PRODUCER_IMAGE) -f java/docker-images/Dockerfile.producer java/

	@echo "Building multi-arch: $(CONSUMER_IMAGE)"
	-podman manifest rm $(CONSUMER_IMAGE) 2>/dev/null
	podman manifest create $(CONSUMER_IMAGE)
	podman build --platform linux/amd64,linux/arm64 --manifest $(CONSUMER_IMAGE) -f java/docker-images/Dockerfile.consumer java/


docker_push:
	 podman manifest push $(PRODUCER_IMAGE)
	podman manifest push $(CONSUMER_IMAGE)

clean:
	mvn clean
