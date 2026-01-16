import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { ProductsComponent } from './products/products.component';
import { CustomersComponent } from './customers/customers.component';
import { BillsComponent } from './bills/bills.component';

import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent,
    ProductsComponent,
    CustomersComponent,   // ✔ Ajout
    BillsComponent        // ✔ Ajout
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule      // ✔ Ajout important
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
