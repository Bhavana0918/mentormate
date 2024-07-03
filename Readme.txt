Create Dockerfile
Create Docker-compose.yml


#docker cmd
-docker build -t mentormate:latest .
-docker images(select and add mentormate:latest this image to docker-compose)
-docker-compose up
-docker ps 
To check MySQL connection
-docker exec -it <docker container id> bash
-mysql -uadmin -ppassword

#push docker image to aws ecr
docker tag mentormate:latest 122238031788.dkr.ecr.ap-south-1.amazonaws.com/mentormate:latest
docker tag mysql:8.1 122238031788.dkr.ecr.ap-south-1.amazonaws.com/mentormate:mysql
aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 122238031788.dkr.ecr.ap-south-1.amazonaws.com
docker push 122238031788.dkr.ecr.ap-south-1.amazonaws.com/mentormate:latest
docker push 122238031788.dkr.ecr.ap-south-1.amazonaws.com/mentormate:mysql


EKS:
eksctl create cluster --name mentormatecluster --region ap-south-1 --nodegroup-name linux-nodes --node-type t2.micro --nodes 2 --nodes-min 1 --nodes-max 3 --managed
aws eks --region ap-south-1 update-kubeconfig --name mentormatecluster
kubectl apply -f mysql-pv.yaml
kubectl apply -f mysql-pvc.yaml
kubectl apply -f mysql-service.yaml
kubectl apply -f mysql-deployment.yaml

kubectl exec -it mysql-57c44cf6c6-fjzrs -- /bin/bash
bash-4.4# mysql -padmin -ppassword

kubectl get pods
kubectl logs <pod name>
kubectl get service


kubectl apply -f mysql-pv.yaml
kubectl apply -f mysql-pvc.yaml
kubectl apply -f mysql-service.yaml
kubectl apply -f mysql-deployment.yaml

kubectl get pods

created manifest files:
pv pvc service deployment files