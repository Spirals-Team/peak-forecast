from fabric.api import task, env
@task
def properties(namehost,username,passworduser):
    env.host_string=namehost
    env.user=username
    env.password=passworduser
