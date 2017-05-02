//
//  UserProfileTableView.swift
//  HALOS
//
//  Created by Brice Buccolo on 4/7/17.
//  Copyright © 2017 adminbbccolo. All rights reserved.
//

import Foundation
import UIKit
class UserProfileTableView: UITableViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        let insets = UIEdgeInsets(top: 100, left: 100, bottom: 100, right: 100)
        tableView.contentInset = insets
        //tableView.rowHeight = 150
        tableView.estimatedRowHeight = 65
    }
}
