function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

/**
 * 获取手机验证码
 * @param data
 * @returns {*}
 */
function sendMsg(data) {
    return $axios({
        'url': '/user/sendMsg',
        'method': 'post',
        data
    })
}
  