
String libName = 'ogci'
String version = 'bugs/1'
String clonePath = [libName, version].join('/')
String gitUrl = "git@github.com:ops-guru/${libName}.git"

def testLibrary = library()
    .name(libName)
    .retriever(gitSource(gitUrl))
    .targetPath(clonePath)
    .defaultVersion(version)
    .allowOverride(true)
    .implicit(false)
    .build()
helper.registerSharedLibrary(testLibrary)
    
loadScript("jobs/tests/testUtilities.groovy")
printCallStack()
