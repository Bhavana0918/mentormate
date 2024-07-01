apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
      - name: mysql
        image: mysql:8.1
        ports:
        - containerPort: 3306
        env:
        - name: MYSQL_DATABASE
          value: mentormate
        - name: MYSQL_USER
          value: admin
        - name: MYSQL_PASSWORD
          value: password
        - name: MYSQL_ROOT_PASSWORD
          value: password
        volumeMounts:
        - name: mysql-storage
          mountPath: /var/lib/mysql
      volumes:
      - name: mysql-storage
        persistentVolumeClaim:
          claimName: mysql-pvc
