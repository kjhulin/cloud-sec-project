/**
 * Encrypted Dropbox with Search
 * Authors: Kevin Hulin, Camron Quitugua, Donald Talkington
 * Date: 12/2/2011
 * Instructor: Dr. Murat Kantarcioglu
 * 
 * Summary: In this project, we developed a program to provide secure,
 * encrypted storage and search for files using the Dropbox cloud storage service.
 * 
 * Language(s) used: Java 1.6 (developed using Eclipse and Netbeans)
 * 
 * Compile with: "javac *.java; javac crypto/*.java"
 * Run with "java MainWindow"
 * 
 * Implementation:
 * This program interfaces with a user's already existing drop box account.
 * The user will be asked to login and is authenticated using the OAuth protocol.
 * Once authenticated for the first time, a new folder will be created in the user's
 * dropbox called "dropboxSSE".  The user's token is also stored on the machine
 * for future logins and persists until the user click "logout".
 * 
 * Once logged in, a user is able to perform operations on the files within the 
 * application folder.
 * Add a file - The user can select a plaintext file from their machine to be
 * encrypted and uploaded to dropbox.  They will be asked for an encryption password
 * as well as a search password.  The search password is required to perform search
 * operations on the user's files without leaking information about the user's files
 * to dropbox.  This password must be be same each time a new file is added and is 
 * verified using the .search file.  The encryption password is the key used
 * to encrypt the file (salted AES-128 CTR mode with salted HMAC SHA-256 hash)
 * 
 * Remove a file/directory - Securely deletes files and associated keyword files
 * using 10 time wiping with null bytes.  Selecting a directory recursively deletes
 * all files and subdirectories.
 * 
 * Edit a file - Brings up a new window that allows the user to make modifications
 * to the selected file.
 * - Keywords List - The user will be prompted for the search password.
 * After authenticating, the keywords are displayed.  The user may now add/remove
 * keywords from this list.  After making changes to the list, the user selects
 * "save keywords" to push the changes made to dropbox.
 * 
 * - Download/Modify the contents of the file - The user will be prompted for the
 * encryption password and will provide a location on the local machine to save the
 * decrypted file.  Optionally, the user may choose to have the program attempt to 
 * automatically open the file using the system's default editor.
 * 
 * The user can also upload a new or modified file.  The user again browses
 * for the file to upload and provides the encryption password. He/she also
 * has the option to securely delete the plaintext file as well (secure against 
 * file carving tools).
 * 
 * 
 * Search for keywords - The user provides the search password and creates a list
 * of keywords to search for.  The program generates trapdoors for each keyword
 * and attempts to match them against a database of trapdoor, file descriptor 
 * pairs.
 * 
 * Matches are returned to the user in the results pane.
 * 
 */