bookmarks_vm_ip=$(shell aws ec2 describe-instances --filters "Name=tag:App,Values=bookmarks" | jq '.Reservations[].Instances[0].PublicIpAddress' -r)
git_version=$(shell git rev-parse --short HEAD)

setup:
	brew install ansible awscli jq
	ansible-galaxy role install geerlingguy.docker geerlingguy.pip

build:
	time docker build . -t bookmarks:latest

vm:
	cd ansible && time ansible-playbook make_vm.yml --connection=local

config-os:
	cd ansible && time ansible-playbook -i inventories/aws_ec2.yml --vault-password-file ~/.bookmarks_vault_password config_os.yml -u ubuntu -b

deploy:
	cd ansible && time ansible-playbook -i inventories/aws_ec2.yml --vault-password-file ~/.bookmarks_vault_password deploy.yml -u ubuntu -b -e "git_version=$(git_version)"

ssh:
	ssh ubuntu@$(bookmarks_vm_ip)

edit-vault:
	ansible-vault edit --vault-password-file ~/.bookmarks_vault_password ./ansible/inventories/group_vars/all/vault
