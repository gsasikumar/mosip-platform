import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-parent',
  templateUrl: './parent.component.html',
  styleUrls: ['./parent.component.css']
})
export class ParentComponent implements OnInit {

  componentName: string;

  constructor() { }

  ngOnInit() {
  }

  onActivate($event) {
    console.log($event);
    this.componentName = $event.route.component.name;
  }

}
