angular.module('ImgurService', [])

// Imgur Service
.service('uploadImageOnImgur', function ($http) {
    return function (file) {
        if (file == null) return;
        return $http({
                method: 'POST',
                url: 'https://api.imgur.com/3/image',
                headers: {
                    Authorization: 'Client-ID b790f7d57013adb',
                    Accept: 'application/json'
                },
                data: {
                    'image': file.base64
                }
            })
            .error(function (err) {
                console.log(err);
            });
    };
});