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
helm install dynatrace-operator oci://public.ecr.aws/dynatrace/dynatrace-operator --create-namespace --namespace dynatrace --atomic



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

kubectl apply -f webapp-pv.yaml
kubectl apply -f webapp-pvc.yaml
kubectl apply -f webapp-service.yaml
kubectl apply -f webapp-deployment.yaml

kubectl get pods
kubectl get svc
kubectl exec -it mysql-6dff9b94d5-87r2t -n mentormate -- bash


created manifest files:
pv pvc service deployment files



eksctl utils install-cluster-autoscaler --name mentormatecluster --region ap-south-1




Intergration of Dynatrace with eks

Create IAM role for Clusker->choose AmazonEKSClusterPolicy
IAM role for Worker node attack->AmazonEC2ContainerRegistryReadOnly,AmazonEks_CNI_Policy,AmazonEKSWorkerNodePolicy



command to update nodes


#command to check pod communication
#kubectl run -it --rm --image=busybox debug -- /bin/sh   (ping <IP>)


#Integration of kubectl with EKS
On Dynatrace -> deployment->oneagent->Kubenetes->generate operation token run helm command
kubectl create namespace dynatrace
kubectl apply -f https://github.com/Dynatrace/dynatrace-operator/releases/download/v1.2.2/kubernetes.yaml
kubectl -n dynatrace wait pod --for=condition=ready --selector=app.kubernetes.io/name=dynatrace-operator,app.kubernetes.io/component=webhook --timeout=300s
kubectl -n dynatrace create secret generic dynakube --from-literal="apiToken=dt0c01.RU5UZZSLVDT252Z2UY4JD4CO.Q4UWJRPQV3WLEPR2YPRHCEQX2OHDP72AZ7X4BALM57TFOWTNPQALR63KQN2XL6UZ"
kubectl apply -f https://github.com/Dynatrace/dynatrace-operator/releases/latest/download/kubernetes-csi.yaml
kubectl apply -f dynakube.yaml








kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

D:\Dynatrace-EKS-integration\mentormate>kubectl create clusterrolebinding dynatrace-oneagent-cluster-admin-binding --clusterrole=cluster-admin --serviceaccount=dynatrace:dynatrace-oneagent
clusterrolebinding.rbac.authorization.k8s.io/dynatrace-oneagent-cluster-admin-binding created




update the launch template volumes


aws ec2 describe-launch-templates --launch-template-name eksctl-mentormatecluster-nodegroup-linux-nodes
aws ec2 create-launch-template-version --launch-template-name eksctl-mentormatecluster-nodegroup-linux-nodes --source-version 1 --launch-template-data "{\"BlockDeviceMappings\":[{\"DeviceName\":\"/dev/xvda\",\"Ebs\":{\"VolumeSize\":50}}]}"
aws eks update-nodegroup-config --cluster-name mentormatecluster --nodegroup-name linux-nodes --launch-template "name=eksctl-mentormatecluster-nodegroup-linux-nodes" --version 2



helm install dynatrace-operator oci://public.ecr.aws/dynatrace/dynatrace-operator --create-namespace --namespace dynatrace --atomic





##########Remotely monitor MySQL databases where you cannot install an OneAgent.############

Get started
Activating the extension
Activate Extension in the Hub by going to:
Extensions → MySQL → Add to environment
Add a new monitoring configuration for every MySQL instance you'd like to monitor
Creating a MySQL user
Create a user that is identified by a native password, customize the username and password as you please

CREATE USER 'dynatrace'@'%' IDENTIFIED WITH mysql_native_password BY 'password';

Give the user the permissions:

GRANT SELECT ON performance_schema.* TO 'dynatrace'@'%';
Allows the user to query the performance_schema schema
GRANT PROCESS ON *.* TO 'dynatrace'@'%';
Allows the user to see thread and connection metrics for other users
GRANT SHOW DATABASES ON *.* TO 'dynatrace'@'%';
Allows the user to see database metrics for all databases
GRANT SELECT ON mysql.slow_log TO 'dynatrace'@'%';
Allows the user to query slow queries
GRANT SELECT ON sys.x$memory_global_by_current_bytes TO 'dynatrace'@'%';
Allow the user to query memory statistics
Collecting Infrastructure metrics
To enable CPU metrics collection, run this query on the MySQL instance:

SET GLOBAL innodb_monitor_enable='cpu%';

Collecting Top Slow Queries
Enable slow queries logging to a table:

SET GLOBAL log_output = 'TABLE';
SET GLOBAL slow_query_log = 'ON';

The default slow query threshold is 10 seconds You can chose the threshold of what is a "slow query" by executing:

SET GLOBAL long_query_time = 2;

This would set slow queries threshold to 2 seconds.

Execution Plan Fetching
To fetch execution plans, you must create a stored procedure for the dynatrace user:

CREATE SCHEMA IF NOT EXISTS dynatrace;

DELIMITER $$
CREATE PROCEDURE dynatrace.dynatrace_execution_plan(IN query TEXT)
    SQL SECURITY DEFINER
BEGIN
    SET @explain := CONCAT('EXPLAIN FORMAT=JSON ', query);
    PREPARE stmt FROM @explain;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END $$
DELIMITER ;

And then gran execution permission for the dynatrace user

GRANT EXECUTE ON PROCEDURE dynatrace.dynatrace_execution_plan TO 'dynatrace'@'%';





