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
The folder has only 3 properties:
The ***name***, ***parent*** and ***contents*** of 
this folder (children).
#### Name
```java
// get the folder name
folder.getName();

// get folder parent
folder.getParent();
        
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
A file differs from a folder in only three ways:
1. He has no children, and therefore nothing can be moved into him.
2. The file has its contents (data) with which you can interact.
3. The file has its coordinates in the container file.

Based on these differences, the file has many
use cases

#### Move File
In addition to the already discussed method,
you can use the method `moveTo`:
```java
fileByExists.moveTo(destinationFolder);
```

#### Write new Content
The `writeContent` method overwrites the entire 
contents of the file and saves it automatically.:
```java
try {
    fileByExists.writeContent("Hello, world!".getBytes(StandardCharsets.UTF_8));
} catch (IOException e) {
    // TODO: handle exception
}
```
#### Append Content
The rpo method appends the passed information 
to the contents of the file and saves it automatically.
```java
try {
    fileByExists.append("Hello, world!".getBytes(StandardCharsets.UTF_8));
} catch (IOException e) {
    // TODO: handle exception
}
```
#### Set data and close
The oop function changes the data of the file in RAM,
but does not affect the data on the hard disk. In order
for information about the new contents of the file from
RAM to get to the hard disk, you must call the close method.
Important! With such a merge, all data that was on the hard
disk about this file will be overwritten. The same applies
to reading from a hard disk into RAM, data from a file in RAM.
```java
fileByExists.setData("Hello, world!".getBytes(StandardCharsets.UTF_8));
fileByExists.close();
```

#### Read From disk
To read information from the disk, you can use `readContent`,
then the information will be saved to RAM. In this case,
you can take it with the help of `getData`.
```java
fileByExists.readContent();
fileByExists.getData();
```
### Save and Restore Tree
To save the tree, you need to call the save function on the 
file system object.
```java
fileSystem.save();
```
To restore the system, you need to call the static method
`openExisted` on the `FileSystem` class
```java
fileSystem.save();
```








































