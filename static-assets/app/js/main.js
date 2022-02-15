/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

(function ({ content, search, classes }, { operators }) {
  const pathRegExp = /^\/(.*?)\.xml$/;

  function getCookie(name) {
    var v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return v ? v[2] : null;
  }
  function isAuthoring() {
    const html = document.documentElement;
    const attr = html.getAttribute('data-craftercms-preview');

    return (
      attr === '${modePreview?c}' || // Otherwise disable/enable if you want to see pencils in dev server.
      attr === 'true'
    );
  }
  function getICE(model, fieldId = null) {
    return (craftercms?.guest?.getICEAttributes({
      model,
      fieldId,
      isAuthoring: authoring
    }))
  }

  const authoring = isAuthoring();
  const { crafterConf } = classes;

  crafterConf.configure({
    baseUrl: '',
    site: getCookie('crafterSite')
  });

  const { map } = operators;
  const { createQuery } = search;
  const searchContent = search.search;
  const { parseDescriptor, preParseSearchResults } = content;

  var browser = new Vue({
    el: '#browser',
    data: {
      types: [
        {
          id: '1',
          label: 'January',
          labelField: 'internal-name',
          contentType: '/component/fact'
        },
        {
          id: '2',
          label: 'February',
          labelField: 'internal-name',
          contentType: '/component/fact'
        },
        {
          id: '3',
          label: 'March',
          labelField: 'internal-name',
          contentType: '/component/fact'
        },
        {
          id: '4',
          label: 'April',
          labelField: 'internal-name',
          contentType: '/component/fact'
        },
       {
          id: '5',
          label: 'May',
          labelField: 'internal-name',
          contentType: '/component/fact'
        },
        {
          id: '6',
          label: 'June',
          labelField: 'internal-name',
          contentType: '/component/fact'
        },
        {
          id: '7',
          label: 'July',
          labelField: 'internal-name',
          contentType: '/component/fact'
        },
        {
          id: '8',
          label: 'August',
          labelField: 'internal-name',
          contentType: '/component/fact'
        },
       {
          id: '9',
          label: 'September',
          labelField: 'internal-name',
          contentType: '/component/fact'
        },
       {
          id: '10',
          label: 'October',
          labelField: 'internal-name',
          contentType: '/component/fact'
        },
       {
          id: '11',
          label: 'November',
          labelField: 'internal-name',
          contentType: '/component/fact'
        },
       {
          id: '12',
          label: 'December',
          labelField: 'internal-name',
          contentType: '/component/fact'
        },
     
  
      ],
      selectedType: null,
      items: [],
      selectedItem: null,
      selectedItemNumUpdates: 0,
    },
    methods: {
      setType: function (type) {
        this.selectedType = type;
        this.selectedItem = null;
        var self = this;

        searchContent(
          createQuery('elasticsearch', {
            'query': {
              'bool': {
                'filter': [
                  {
                    'match': {
                      'content-type': type.contentType
                    },

                    'match': {
                      'factMonth_s': type.id
                    }
                  }
                ]
              }
            },
            'sort': {
              'createdDate_dt': { 'order': 'desc' }
            }
          })
        ).pipe(
          map(({ hits, ...rest }) => ({
            ...rest,
            hits: hits.map(({ _source }) => parseDescriptor(
              preParseSearchResults(_source)
            ))
          }))
        ).subscribe((results) => {
          self.items = results;
        });
      },
      setItem: function (item) {
        this.selectedItem = item;
        this.selectedItemNumUpdates = 0;
      },
      updateItem: function(args) {
        const itemIndex = this.items.hits.findIndex(item => {
          return item.craftercms.id === args.modelId
        });

        this.items.hits[itemIndex][args.fieldId] = args.value;
      },
      deregisterItems: function() {
        document.querySelectorAll('[data-craftercms-model-id]').forEach((el) => {
          const record = craftercms.guest.elementRegistry.fromElement(el);

          // This is supposed to be before updating DOM, but query is returning both old and new elements
          if (record) {
            craftercms?.guest?.elementRegistry.deregister(record.id);
          }
        });
      },
      registerItems: function() {
        document.querySelectorAll('[data-craftercms-model-id]').forEach((element) => {
          let //
            path = element.getAttribute('data-craftercms-model-path'),
            modelId = element.getAttribute('data-craftercms-model-id'),
            fieldId = element.getAttribute('data-craftercms-field-id'),
            index = element.getAttribute('data-craftercms-index'),
            label = element.getAttribute('data-craftercms-label');

          if ((index !== null) && (index !== undefined) && !index.includes('.')) {
            // Unsure if somewhere, the system relies on the index being an integer/number.
            // Affected inventory:
            // - Guest.moveComponent() - string type handled
            index = parseInt(index, 10);
          }

          craftercms?.guest?.elementRegistry.register({ element, modelId, fieldId, index, label, path });
        });
      },
      getICE
    },
    watch: {
      selectedItem: function() {
        this.deregisterItems();

        this.$nextTick(function() {
          if (this.selectedItem) {
            this.registerItems();
          }
        });
      }
    },
    mounted: function() {
      const self = this;
      $(function () {
        const sub = craftercms.guest?.contentController?.operations$.subscribe((op) => {
          if (op.type === 'UPDATE_FIELD_VALUE_OPERATION') {
            self.updateItem(op.args);
            self.selectedItemNumUpdates++;
          }
        });
      })
    }
  });
})(craftercms, rxjs);