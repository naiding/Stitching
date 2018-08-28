(function () {

    init();

    var imgFiles = new Array();
    
    function init() {
        console.log("Hello");
        $("login-btn").addEventListener('click', login);    
        $("signup-btn-mainpage").addEventListener('click', signup);
        $("signup-btn-signuppage").addEventListener('click', register);
        $("inputGroupFile02").addEventListener("change", showImages);
        $("upload-btn").addEventListener("click", uploadImages);
        
        onSessionInvalid();
        validateSession();   //todo
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
//          alert('upload successfully.');
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
    
    
    function validateSession() {
        // The request parameters
        var url = './login';
        var req = JSON.stringify({});

        // make AJAX call
        ajax('GET', url, req,
        // session is still valid
        function(res) {
            var result = JSON.parse(res);

            if (result.status === 'OK') {
                onSessionValid(result);
            }
        });
    }
    
    function onSessionValid(result) {
    	var loginSection = $('login-section');
        var mainSection = $('main-section');
        var signupSection = $('signup-section');

        showElement(mainSection);
        hideElement(loginSection);
        hideElement(signupSection);
    }
    
    
    
    function onSessionInvalid() {
        var loginSection = $('login-section');
        var mainSection = $('main-section');
        var signupSection = $('signup-section');

        hideElement(mainSection);
        hideElement(signupSection);


        showElement(loginSection);
    }
    
    function onSessionInvalid2() {
        var loginSection = $('login-section');
        var mainSection = $('main-section');
        var signupSection = $('signup-section');

        hideElement(mainSection);
        hideElement(loginSection);


        showElement(signupSection);
    }
    
    
    // -----------------------------------
    // Login
    // -----------------------------------

    function login() {
        var username = $("username-login").value;
        var password = $("password").value;
        password = md5(username + md5(password));

        // The request parameters
        var url = './login';
        var req = JSON.stringify({
            username : username,
            password : password,
        });
        console.log(password);
        ajax('POST', url, req, function(res) {
			var result = JSON.parse(res);

			// successfully logged in
			if (result.status === 'OK') {
				onSessionValid(result);
			}
		});
    }
    
    // -----------------------------------
    // Signup
    // -----------------------------------
    
    function signup() {
    	onSessionInvalid2();
    }
    
    function register() {
    	var username = $("username-signup").value;
    	var email = $("email-signup").value;
        var password = $("password-signup").value;
        password = md5(username + md5(password));

        // The request parameters
        var url = './register';
        var req = JSON.stringify({
            username : username,
            email: email,
            password : password,
        });
        console.log(password);
        ajax('POST', url, req, function() {
        	location.reload();
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
    
    function hideElement(element) {
        element.style.display = 'none';
    }
    
    function showElement(element, style) {
        var displayStyle = style ? style : 'block';
        element.style.display = displayStyle;
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
                callback(xhr.responseText);
            } else if (xhr.status === 401) {
            	console.log("verification failed");
//                onSessionInvalid();
            } else {
            	console.log("error in ajax");
//                errorHandler();
            }
        };

       // xhr.withCredentials = credentials;

        xhr.onerror = function() {
            console.error("The request couldn't be completed.");
        };

        if (data === null) {
            xhr.send();
        } else {
            xhr.setRequestHeader("Content-Type",
                "application/json;charset=utf-8");
            xhr.send(data);
        }
    }

})();