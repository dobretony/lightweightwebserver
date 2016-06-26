**Lightweight Web Server**

1. Introduction

   This project is an Open Source implementation of a Web Server, conforming
with the [HTTP 1.1 specification](https://tools.ietf.org/html/rfc2616).
 
   The server is written in Java and has been done as an exercise. It is
by no means a complete solution, as for example you cannot add dynamic 
generated content, such as PHP, Servlets or any other type.
  
  The server can respond to GET, POST, HEAD and OPTIONS method requests
and has a structure that permits expansion and scalability.

2. Project Structure

    The Web Server is structured as follows:
    
    src ( the project sources )
     |
     |- main ( Java packages )
     |   |
     |   |- com.ppb.lightweight.web.server
     |   |      |- main
     |   |      |    |- LightweightWebServerMain (main entry point of server)
     |   |      |
     |   |      |- handlers ( Contains Handlers that can resolve client requests
     |   |      |             e.g. HTTPHandler )
     |   |      |
     |   |      |- errors ( Implementations of Exceptions and Errors used
     |   |      |           throughout the project )
     |   |      |
     |   |      |- internal ( The classes necessary for the internal workings of
     |   |      |             the web server )
     |   |      |
     |   |      |- http ( The implementation of the HTTP protocol, with Request
     |   |      |         objects, Response objects  and constants)
     |   |      |
     |   |      |- logger ( The implementation of the Logger utilities )
     |   |      |
     |   |      |- utils ( Utility methods that are used throughout the server)
     |- tests ( the tests folder for this project )
     |
    logs ( the output of the server )
     |
     |
    build ( the result of grade build )
     |
     |
    www ( directory that contains the web server resources )
     |
     |
    ligthweightwebserver ( Linux start-stop script )
     |
     |
    lightweightwebserver.config ( Configuration File )
    
    The project is all self made, only with various code chunks copied
 from StackOverflow or similar tutorial websites ( it is specified in a
 comment where this occurs ).
 
    The core of the application is the LightweightWebServer class that
 listens for client connections, creates the Handler for that client
 and passes the request resolution to a new thread managed by the Handler.
 At the moment, only a HTTPHandler is present and it handles all things
 HTTP 1.1 related.
 
    Basically, when a request is received, the HTTP handler starts,
 creates a HTTPRequest object that holds all the header files, content
 and parameters sent in the request. Based on this object, the handler
 also creates a HTTPResponse that is sent back to the client.
 
 3. Building the project
 
    In order to build this project, you will need to have Gradle 
 installed on your machine.
    Once you pulled the source code, you can run "gradle build" .
    After the project is build, you can find the .jar in build/libs folder.
    
 4. Using the server
 
    Windows
    
    The project was not tested on Windows, but because it was written
 with portability in mind, it should not be a problem to run the server
 using:
 
    java -jar lightweightwebserver-1.0.jar
    
    Linux
    
    On Linux there is also a start/stop script named "lightweightwebserver"
 in the root folder of this project. With this script you can call:
 
    lighweightwebserver start
    
 in order to start the server after it was built and:
 
    lightweightwebserver stop
    
 to stop the server.
 
 5. Configuring the web server
 
    The Web server accepts a range of configuration options that can be
 overwritten by modifying the "lightweightwebserver.config" file.
 
    The configurations available are listed in utils.Configurations,
 and the general syntax of the config file is a key=value pair based on
 the static variable names from that class.
    As an example, a basic config file looks like:
    
    server_hostname="LightweightWebServer"
    server_ip_address=10.0.2.15
    port_number=8080
    no_of_active_conn=50
    
    A list of all the configurations available are:
    
    debug=yes/" " # sets the debug output of the logger
    verbose=yes/" " # set the verbose output of the logger
    www_folder_path="" # the path to the root folder of resources
    log_folder_path="" # the path to the folder where logs should be kept
    server_hostname="" # the server host name
    server_ip_address="" # the ip address the socket should bind to
    port_number="" # the port number the socket should bind to
    client_socket_timeout="" # the client socket timeout
    no_of_active_conn="" # the number of active connections at any given time
    max_length_of_request_content="" # the max number of bytes a request content should have
    file_not_found_message_file="" # the file not found message file
    
 
  
    
 


