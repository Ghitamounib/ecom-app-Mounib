import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-bills',
  templateUrl: './bills.component.html',
  styleUrls: ['./bills.component.css']
})
export class BillsComponent implements OnInit {

  customerId!: number;
  bills: any;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  ngOnInit(): void {

    // récupérer l'ID du client depuis l'URL
    this.customerId = this.route.snapshot.params['customerId'];

    // URL via Gateway
    const url = `http://localhost:8888/billing-service/api/bills/search/byCustomerID?customerID=${this.customerId}`;

    // appel HTTP au billing-service via Gateway
    this.http.get(url).subscribe({
      next: (data) => {
        this.bills = data;
        console.log("Bills reçues :", data);
      },
      error: (err) => {
        console.error("Erreur lors du chargement des bills :", err);
      }
    });
  }
}
