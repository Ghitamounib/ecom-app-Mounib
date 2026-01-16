import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductsComponent } from './products/products.component';
import { CustomersComponent } from './customers/customers.component';
import { BillsComponent } from './bills/bills.component';

const routes: Routes = [
  { path: "products", component: ProductsComponent },
  { path: "customers", component: CustomersComponent },

  // ✔ Route dynamique pour les factures
  { path: "bills/:customerId", component: BillsComponent },

  // optionnel : redirection par défaut
  { path: "", redirectTo: "products", pathMatch: "full" }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
