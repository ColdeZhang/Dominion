import {defineNoteConfig, defineNotesConfig} from 'vuepress-theme-plume'

const DemoNote = defineNoteConfig({
    dir: 'demo',
    link: '/demo',
    sidebar: ['', 'foo', 'bar'],
})

const playerDoc = defineNoteConfig({
    dir: 'doc/player',
    link: '/notes/doc/player',
    sidebar: 'auto',
})

const ownerDoc = defineNoteConfig({
    dir: 'doc/owner',
    link: '/notes/doc/owner',
    sidebar: 'auto',
})

const apiReference = defineNoteConfig({
    dir: 'api',
    link: '/notes/api',
    sidebar: 'auto',
})

const nodeList = [DemoNote, playerDoc, ownerDoc, apiReference]

/* =================== locale: zh-CN ======================= */

export const zhNotes = defineNotesConfig({
    dir: 'notes',
    link: '/',
    notes: nodeList,
})


/* =================== locale: en-US ======================= */

export const enNotes = defineNotesConfig({
    dir: 'en/notes',
    link: '/en/',
    notes: nodeList,
})

/* =================== locale: ja-JP ======================= */

export const jpNotes = defineNotesConfig({
    dir: 'jp/notes',
    link: '/jp/',
    notes: nodeList,
})

