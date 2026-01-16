import { Component, OnInit } from '@angular/core';
import { HttpClient } from "@angular/common/http";

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {

  products: any = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get("http://localhost:8888/inventory-service/api/products").subscribe({
      next: (data) => {
        this.products = data;
        console.log("Produits chargés :", this.products);
      },
      error: (err) => {
        console.error("Erreur API :", err);
      }
    });
  }

}
