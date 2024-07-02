apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql
spec:
  selector:
    matchLabels:
      app: mysql
  strategy:
    type: Recreate
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
      - image: 122238031788.dkr.ecr.ap-south-1.amazonaws.com/mysql:8.1
        name: mysql
        env:
        - name: MYSQL_DATABASE
          value: "mentormate"
        - name: MYSQL_USER
          value: "admin"
        - name: MYSQL_PASSWORD
          value: "password"
        - name: MYSQL_ROOT_PASSWORD
          value: "password"
        ports:
        - containerPort: 3306
          name: mysql
        volumeMounts:
        - name: mysql-persistent-storage
          mountPath: /var/lib/mysql
      volumes:
      - name: mysql-persistent-storage
        persistentVolumeClaim:
          claimName: mysql-pvc
