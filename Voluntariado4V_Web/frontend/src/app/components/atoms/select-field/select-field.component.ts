import { Component, Input, forwardRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
    selector: 'app-select-field',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './select-field.component.html',
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => SelectFieldComponent),
            multi: true
        }
    ]
})
export class SelectFieldComponent implements ControlValueAccessor {
    @Input() label: string = '';
    @Input() placeholder: string = 'Selecciona...';
    @Input() options: { value: any, label: string }[] = [];
    @Input() errorMessage: string = '';
    @Input() isInvalid: boolean = false;

    value: any = null;
    isDisabled: boolean = false;

    onChange = (value: any) => { };
    onTouched = () => { };

    writeValue(value: any): void {
        this.value = value;
    }

    registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        this.isDisabled = isDisabled;
    }
}
