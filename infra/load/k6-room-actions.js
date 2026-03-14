import http from 'k6/http'
import { check, sleep } from 'k6'

export const options = {
  scenarios: {
    room_actions: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        { duration: '30s', target: 10 },
        { duration: '60s', target: 30 },
        { duration: '30s', target: 0 },
      ],
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.02'],
    http_req_duration: ['p(95)<750'],
  },
}

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080'

export default function () {
  const roomResponse = http.post(`${BASE_URL}/api/rooms`, JSON.stringify({ name: 'LoadRoom' }), {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${__ENV.ACCESS_TOKEN}`,
    },
  })

  check(roomResponse, {
    'room create 2xx': (r) => r.status >= 200 && r.status < 300,
  })

  sleep(0.2)
}
