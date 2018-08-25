(function () {

    init();

    var imgFiles = new Array();
    
    function init() {
        console.log("Hello");
        	
        $("inputGroupFile02").addEventListener("change", showImages);
        $("upload-btn").addEventListener("click", uploadImages);
    }

    function showImages() {
        var fileDom = $("inputGroupFile02");
        var previewDom = $("preview");
       
        function readAndPreview(file) {
            // Make sure `file.name` matches our extensions criteria
            if ( /\.(jpe?g|png|gif)$/i.test(file.name) ) {
            		var reader = new FileReader();
            		reader.addEventListener("load", function () {
            			var div = $("div", {
	  					className : "col-3"
	  				});
	  				div.appendChild($("img", {
	  					className : "img-thumbnail",
	  					src : this.result,
	  					alt : "screenshot"
	  				}))
	  				previewDom.appendChild(div);
            		}, false);
            		imgFiles.push(file);
            		reader.readAsDataURL(file);
        		}
        }

        if (fileDom.files) {
        		[].forEach.call(fileDom.files, readAndPreview);
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
		});
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
    function ajax(method, url, data, callback, errorHandler) {
        var xhr = new XMLHttpRequest();
        xhr.open(method, url, true);
        xhr.responseType = "blob";
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