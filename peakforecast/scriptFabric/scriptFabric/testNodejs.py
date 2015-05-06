@task(alias='testNodejs')
def configImage4():
    print("DEBUT Test node.js")
    print("Debut copy example helloword") 
    put('/home/daniel/tmp/hello.js','hello.js')
    print("copy completed") 
    run('node ./hello')
    print("END Test node.js")
