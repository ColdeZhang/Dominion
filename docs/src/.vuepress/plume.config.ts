import {defineThemeConfig} from 'vuepress-theme-plume'
import {enNavbar, jpNavbar, ruNavbar, zhNavbar} from './navbar'
import {enNotes, jpNotes, ruNotes, zhNotes} from './notes'

/**
 * @see https://theme-plume.vuejs.press/config/basic/
 */
export default defineThemeConfig({
    logo: '/logo.png',

    appearance: true,

    social: [
        {icon: 'github', link: 'https://github.com/LunaDeerMC/Dominion'},
    ],

    locales: {
        '/': {
            profile: {
                avatar: '/logo.png',
                name: 'DominionDocumentation',
                description: 'Documentation of Dominion.',
                // circle: true,
                // location: '',
                // organization: '',
            },
            navbar: zhNavbar,
            notes: zhNotes,
        },
        '/en/': {
            profile: {
                avatar: '/logo.png',
                name: 'DominionDocumentation',
                description: 'Documentation of Dominion.',
            },
            navbar: enNavbar,
            notes: enNotes,
        },
        '/jp/': {
            profile: {
                avatar: '/logo.png',
                name: 'DominionDocumentation',
                description: 'Documentation of Dominion.',
            },
            navbar: jpNavbar,
            notes: jpNotes,
        },
        '/ru/': {
            profile: {
                avatar: '/logo.png',
                name: 'DominionDocumentation',
                description: 'Documentation of Dominion.',
            },
            navbar: ruNavbar,
            notes: ruNotes,
        },
    },
})
