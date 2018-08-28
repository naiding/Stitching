(function () {

    init();

    var imgFiles = new Array();
    
    function init() {
        console.log("Hello");
        	
        $("inputGroupFile02").addEventListener("change", showImages);
        $("upload-btn").addEventListener("click", uploadImages);
        
        	validateSession();
    }
    
    /**
     * Session
     */
    function validateSession() {
        // The request parameters
        var url = './login';
        var req = JSON.stringify({});

        // make AJAX call
        ajax('GET', url, req,
        // session is still valid
        function(res) {
            	console.log(res);
        },
        function(res) {
        		console.log(res);
        });
    }

    function showImages() {
    	var files = $("inputGroupFile02").files;
    	for (var i = 0; i < files.length; i++) {
    		var file = files[i];
    		imgFiles.push(file);
    		if ( /\.(jpe?g|png|gif)$/i.test(file.name) ) {
    			var reader = new FileReader();

    			reader.onload = (function(theFile) {
    				return function(e) {
    					var div = $("div", {
    						className : "col-3"
    					});
    					div.appendChild($("img", {
    						className : "img-thumbnail",
    						src : e.target.result,
    						alt : "screenshot"
    					}));
    					$("preview").appendChild(div);
    				};
    			})(file);
    			reader.readAsDataURL(file);
			}
		}
    }

    function uploadImages() {
    	if(imgFiles.length < 2) {
    		alert("Total images number must be larger than 1.");
    		return;
    	} 
    	
        var formData = new FormData();
		for (var i = 0; i < imgFiles.length; i++) {
			formData.append("file", imgFiles[i]);
		}
		var url = './upload';

		ajax('POST', url, formData, 
		function(res) {
//			alert('upload successfully.');
			var blob = res;
			var img = document.createElement("img");
            img.onload = function(e) {
                window.URL.revokeObjectURL(img.src); 
            };
            img.src = window.URL.createObjectURL(blob);
            img.className = "img-thumbnail";
            
            var div = $("div", {
            	className: "col-4"
            })
            div.appendChild(img);
            $("preview").innerHTML = "";
            $("preview").appendChild(div);
		},

		function() {
			alert('upload failed.');
		}, "blob");
    }

    /**
	 * A helper function that creates a DOM element <tag options...>
	 * 
	 * @param tag
	 * @param options
	 * @returns
	 */
    function $(tag, options) {
        if (!options) {
            return document.getElementById(tag);
        }

        var element = document.createElement(tag);

        for (var option in options) {
            if (options.hasOwnProperty(option)) {
                element[option] = options[option];
            }
        }
        return element;
    }

    /**
     * AJAX helper
     *
     * @param method -
     *            GET|POST|PUT|DELETE
     * @param url -
     *            API end point
     * @param callback -
     *            This the successful callback
     * @param errorHandler -
     *            This is the failed callback
     */
    function ajax(method, url, data, callback, errorHandler, responseType) {
        var xhr = new XMLHttpRequest();
        xhr.open(method, url, true);
        	if (responseType) {
            xhr.responseType = responseType;
        	}
        xhr.onload = function() {
            if (xhr.status === 200) {
                callback(xhr.response);
            } else {
                errorHandler();
            }
        };

        xhr.onerror = function() {
            console.error("The request couldn't be completed.");
            errorHandler();
        };

        if (data === null) {
            xhr.send();
        } else {
            xhr.processData = false;
            xhr.send(data);
        }
    }

})();