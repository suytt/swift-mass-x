swift-mass-x
============

Openstack Swift Mass [Uploader | Lister | Remover]

This is a heavily multi-threaded asynchronous client for openstack swift storage.
It allows you to upload many files in parallel, list huge amount of files or delete as many.

Just increase the number of **threads** but beware, this can cause the entire swift to DDoS.
Additionnally, list of files are buffered in ram on the client, so increase the available memory for the program to run (-Xmx)

Build
-----

`javac openstack.java`

Usage
-----

`java [-Xmx2g] -jar openstack.jar [OPTIONS]`

	Options: (* = mandatory)
		-h,  --help      Show the help message
		*-o, --operation Type of operation (DELETE|UPLOAD|LIST)
		-l,  --log-file  Log file name (default: stdout)
		-t,  --threads   Number of parallel threads (default: 2)
		-v,  --verbose   (1|0) Log more than exceptions (default: 0)
		*-d, --directory The local directory to upload or remote directory 
						 to delete (if not set, the container will be dropped)
		*-u, --username  The openstack username
		*-p, --password  The openstack password
		*-s, --server    The openstack server (https://...)
		*-c, --container The openstack container name
