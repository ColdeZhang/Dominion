import {defineNavbarConfig} from 'vuepress-theme-plume'

export const zhNavbar = defineNavbarConfig([
    {text: '首页', link: '/'},
    {
        text: '使用文档',
        items: [
            {text: '玩家手册', link: '/notes/doc/player/README.md'},
            {text: '服主手册', link: '/notes/doc/owner/README.md'},
        ]
    },
    {text: 'API参考', link: '/notes/api/README.md'},
])

export const enNavbar = defineNavbarConfig([
    {text: 'Home', link: '/en/'},
    {
        text: 'Documentation',
        items: [
            {text: 'Player', link: '/en/notes/doc/player/README.md'},
            {text: 'Server Owner', link: '/en/notes/doc/owner/README.md'},
        ]
    },
    {text: 'API Reference', link: '/en/notes/api/README.md'},
])

