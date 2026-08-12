import http from '../http.js'

export default {
  getSummary() {
    return http.get('/api/v1/check-ins/me')
  },
  checkIn() {
    return http.post('/api/v1/check-ins')
  }
}
