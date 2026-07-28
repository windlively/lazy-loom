import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/explorer/explorer').then(m => m.ExplorerComponent)
  },
  {
    path: 'upload',
    loadComponent: () => import('./components/upload/upload').then(m => m.UploadComponent)
  }
];
