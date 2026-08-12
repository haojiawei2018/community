import { defineStore } from 'pinia'

export interface SessionData { token:string; username:string; displayName:string; permissions:string[] }
const KEY = 'gaming_community_merchant_session'
const EMPTY: SessionData = { token:'', username:'', displayName:'', permissions:[] }

function read(): SessionData {
  try { return { ...EMPTY, ...JSON.parse(localStorage.getItem(KEY) || '{}') } }
  catch { localStorage.removeItem(KEY); return { ...EMPTY } }
}

export const useSessionStore = defineStore('session', {
  state: () => ({ current: read() }),
  getters: {
    hasPermission: (state) => (permission?: string) => !permission || state.current.permissions.includes(permission),
  },
  actions: {
    save(session: SessionData) { this.current = session; localStorage.setItem(KEY, JSON.stringify(session)) },
    clear() { this.current = { ...EMPTY }; localStorage.removeItem(KEY) },
  },
})
