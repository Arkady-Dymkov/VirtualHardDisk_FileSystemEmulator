# VirtualHardDisk_FileSystemEmulator
> This task was completed as part of a test task at JetBrains

## Description


## Usage
### Creating a new file system
The creation of a new file system is done with `FileSystemBuilder` class.
The required argument for the builder is the `Path` to the
container file, that will serve as the storage of the file system.
```java
Path pathToDisk = Paths.get("src/test/java/TestFiles/database.jb");

// Creating the new File system builder
FileSystemBuilder fileSystemBuilder = new FileSystemBuilder(pathToDisk);
```
Since this system implements file system storage
in the form of blocks, you can also pass the block 
size in bytes to the builder, if necessary. In general, 
this is not required.
- **The block size must be greater than zero and be
 a multiple of 32**
```java
fileSystemBuilder.setBlockSize(32);
```
Similarly, you can change the name of the default root folder. 
This is also optional, the default root folder is called *"ROOT"*.
```java
fileSystemBuilder.setRootFolderName("C:\\");
```

To finally create the file system, you need to call `build()` on 
builder. This call can throw an exception if the file system is
cannot be created successfully.
```java
try {
    FileSystem fileSystem = fileSystemBuilder.build();
} catch (IOException e) {
    // TODO: Handle exception
}
```

A simplified view of creating a file system looks like this:
```java
Path pathToDisk = Paths.get("src/test/java/TestFiles/database.jb");
FileSystem fileSystem;
try {
    fileSystem = new FileSystemBuilder(pathToDisk)
            .setBlockSize(32)
            .setRootFolderName("C:\\")
            .build();
} catch (IOException e) {
    // TODO: Handle exception
}
```

You can also leave the default values unchanged.
```java
Path pathToDisk = Paths.get("src/test/java/TestFiles/database.jb");
FileSystem fileSystem;
try {
    fileSystem = new FileSystemBuilder(pathToDisk).build();
} catch (IOException e) {
    // TODO: Handle exception
}
```
### Creating a new folder
To create a new folder, you need to call the 
fabric constructor `createFolder` of the abstract 
class `FileSystemObject`, which takes the name of the
folder as a parameter.
Folders are represented by a class `VirtualFolder`.

```java
VirtualFolder folder = FileSystemObject.createFolder("Programming");
```

### Creating a new file
File can be created by two different ways.
1. By Create an empty file with just a name 
2. Create a file from an already existing file in the file system

To create a new file, you also need to call the
fabric constructor `createFileFromExistingFile` or
`createFileByName` of the abstract class `FileSystemObject`.
Folders are represented by a class `VirtualFile`.

```java
// Create file by name
VirtualFile fileByName = FileSystemObject.createFileByName("file.txt");

// Create file by exist folder
try {
    VirtualFile fileByExists = FileSystemObject.createFileFromExistingFile(new File("src/test/java/TestFiles/pdf.pdf"));
} catch (IOException e) {
    e.printStackTrace();
}
```
### Folder Actions
The folder has only 2 properties:
The ***name*** and ***contents*** of this folder (children)
#### Name

```java
// get the folder name
folder.getName();
        
// set new name for the folder
folder.setName("new name");
```

#### Iterate over children
You can freely iterate over the children of
a folder by taking them with a getter
```java
folder.getChildren();
```
#### Add a new child
To add a new child, you should move it to the folder.
You have two options:
- If you want to move a file into a folder, we can call its
own move function `moveTo`
- If you wish to move any object, then you can use the method
`moveHere`

*About actions with files next section after folders*
```java
folder.moveHere(file);
```



#### Deleting a folder
There are two options for deleting a folder
1. Safe when an error is returned if it has content
2. Hard when the folder is deleted along with all the contents

```java
try {
    // Hard delete
    folder.hardDelete();

    // Soft delete
    folder.delete();
} catch (Exception e) {
    // TODO: handle exception
}
```

### File Actions

## Description of the composition of the program








































