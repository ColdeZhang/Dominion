import {viteBundler} from '@vuepress/bundler-vite'
import {defineUserConfig} from 'vuepress'
import {plumeTheme} from 'vuepress-theme-plume'

export default defineUserConfig({
    base: '/',
    lang: 'zh-CN',
    locales: {
        '/': {
            title: 'DominionDocs',
            lang: 'zh-CN',
            description: 'Documentation of Dominion.',
        },
        '/en/': {
            title: 'DominionDocs',
            lang: 'en-US',
            description: 'Documentation of Dominion.',
        },
        '/jp/': {
            title: 'DominionDocs',
            lang: 'ja-JP',
            description: 'Documentation of Dominion.',
        },
    },

    bundler: viteBundler(),

    theme: plumeTheme({
        // 添加您的部署域名
        // hostname: 'https://your_site_url',
        // your git repo url
        docsRepo: 'https://github.com/ColdeZhang',
        docsDir: 'docs',

        blog: false,  // 禁用博客

        plugins: {
            /**
             * Shiki 代码高亮
             * @see https://theme-plume.vuejs.press/config/plugins/code-highlight/
             */
            shiki: {
                languages: ['java', 'yaml', 'kotlin', 'json', 'xml', 'groovy'],
            },

            /**
             * markdown enhance
             * @see https://theme-plume.vuejs.press/config/plugins/markdown-enhance/
             */
            markdownEnhance: {
                demo: true,
                //   include: true,
                //   chart: true,
                //   echarts: true,
                //   mermaid: true,
                //   flowchart: true,
            },

            /**
             *  markdown power
             * @see https://theme-plume.vuejs.press/config/plugin/markdown-power/
             */
            markdownPower: {
                //   pdf: true,
                //   caniuse: true,
                plot: true,
                //   bilibili: true,
                //   youtube: true,
                //   icons: true,
                //   codepen: true,
                //   replit: true,
                //   codeSandbox: true,
                //   jsfiddle: true,
                //   repl: {
                //     go: true,
                //     rust: true,
                //     kotlin: true,
                //   },
            },

            /**
             * 评论 comments
             * @see https://theme-plume.vuejs.press/guide/features/comments/
             */
            // comment: {
            //   provider: '', // "Artalk" | "Giscus" | "Twikoo" | "Waline"
            //   comment: true,
            //   repo: '',
            //   repoId: '',
            //   category: '',
            //   categoryId: '',
            //   mapping: 'pathname',
            //   reactionsEnabled: true,
            //   inputPosition: 'top',
            // },
        },
    }),
})
