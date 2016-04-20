/*
 * MROZA - supporting system of behavioral therapy of people with autism
 *     Copyright (C) 2015-2016 autyzm-pg
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mroza.viewbeans;


import com.mroza.interfaces.KidsService;
import com.mroza.models.Kid;
import com.mroza.qualifiers.KidsServiceImpl;
import com.mroza.viewbeans.utils.NavigationUtil;
import com.mroza.viewbeans.utils.ViewMsg;
import com.mroza.viewbeans.utils.ViewMsgUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@ViewScoped
@Named
@NoArgsConstructor
public class NewKidsBean implements Serializable {

    @Inject
    @KidsServiceImpl
    private KidsService kidsService;

    @Getter
    @Setter
    private Kid kid;

    @PostConstruct
    public void init() {
        kid = new Kid();
    }

    public void saveKid() {
        if (kidsService.existsKidWithCode(kid.getCode())) {
            ViewMsgUtil.setViewErrorMsg(ViewMsg.KID_CODE_DUPLICATED);
        }
        else {
            kidsService.saveKid(kid);
            NavigationUtil.redirect("kidsView.xhtml");
        }
    }
}
