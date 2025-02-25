import {defineThemeConfig} from 'vuepress-theme-plume'
import {enNavbar, jpNavbar, zhNavbar} from './navbar'
import {enNotes, jpNotes, zhNotes} from './notes'

/**
 * @see https://theme-plume.vuejs.press/config/basic/
 */
export default defineThemeConfig({
    logo: '/logo.png',

    appearance: true,

    social: [
        {icon: 'github', link: '/'},
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
                // circle: true,
                // location: '',
                // organization: '',
            },
            navbar: enNavbar,
            notes: enNotes,
        },
        '/jp/': {
            profile: {
                avatar: '/logo.png',
                name: 'DominionDocumentation',
                description: 'Documentation of Dominion.',
                // circle: true,
                // location: '',
                // organization: '',
            },
            navbar: jpNavbar,
            notes: jpNotes,
        },
    },
})
