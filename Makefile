cf_email = $(shell grep api_email ~/.cf_token | cut -d '=' -f 2)
cf_token = $(shell grep api_token ~/.cf_token | cut -d '=' -f 2)

vm_ip=$(shell aws ec2 describe-instances --filters "Name=tag:App,Values=bookmarks" | jq '.Reservations[].Instances[0].PublicIpAddress' -r)

edit-vault:
	cd ansible && ansible-vault edit group_vars/all/vault

ssh:
	ssh ubuntu@$(vm_ip)

setup:
	brew install ansible awscli jq
	ansible-galaxy role install geerlingguy.docker geerlingguy.pip

infra:
	cd ansible && time ansible-playbook 1_infra.yml -e 'cf_email=$(cf_email) cf_token=$(cf_token)'

linux:
	cd ansible && time ansible-playbook -i inventories/aws_ec2.yml 2_linux.yml -u ubuntu -b

deploy:
	cd ansible && time ansible-playbook -i inventories/aws_ec2.yml 3_deploy.yml -u ubuntu -b -e "git_version=$(shell git rev-parse --short HEAD)"

dash:
	echo 'http://localhost:10000'
	ssh -L 10000:localhost:8080 ubuntu@$(vm_ip)
