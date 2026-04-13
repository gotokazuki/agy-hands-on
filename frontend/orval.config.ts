import { defineConfig } from 'orval';

export default defineConfig({
  backoffice: {
    input: '../docs/openapi.yaml',
    output: {
      mode: 'tags-split',
      target: 'src/shared/api/endpoints.ts',
      schemas: 'src/shared/api/model',
      client: 'react-query',
      mock: false,
      override: {
        mutator: {
          path: 'src/shared/api/axios.ts',
          name: 'customInstance',
        },
      },
    },
  },
});
