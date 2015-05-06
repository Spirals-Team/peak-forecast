from fabric.api import *
from fabric.operations import *

@task  
def installMongodbWithLocalPackage(username):
    print("BEGIN install mongodb with Local package")
    print("BEGIN copy package") 
    put('/home/daniel/tmp/mongodb-linux-i686-2.0.6.tgz','mongodb-linux-i686-2.0.6.tgz')
    print("copy completed") 
    print("BEGIN unpack")         
    run('tar zxf mongodb-linux-i686-2.0.6.tgz')   
    run('mv mongodb-linux-i686-2.0.6 mongodb')  
    print("unpack completed")
    sudo('mkdir -p /data/db/')
    sudo("chown "+username+" /data/db")
    print("END install mongodb")
